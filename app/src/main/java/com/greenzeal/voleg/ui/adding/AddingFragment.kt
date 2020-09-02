package com.greenzeal.voleg.ui.adding

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.*
import android.os.*
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.greenzeal.voleg.R
import com.greenzeal.voleg.databinding.FragmentAddingBinding
import com.greenzeal.voleg.di.component.DaggerFragmentComponent
import com.greenzeal.voleg.di.module.FragmentModule
import com.greenzeal.voleg.models.Adv
import com.greenzeal.voleg.models.AdvData
import com.greenzeal.voleg.models.PhoneNumber
import com.greenzeal.voleg.ui.adapter.NumbersAdapter
import com.greenzeal.voleg.ui.list.ListContract
import com.greenzeal.voleg.util.BitmapUtils
import com.greenzeal.voleg.util.Constants
import com.greenzeal.voleg.util.Constants.Companion.REQUEST_GALLERY_PHOTO
import com.greenzeal.voleg.util.Constants.Companion.REQUEST_TAKE_PHOTO
import com.greenzeal.voleg.views.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

class AddingFragment : Fragment(), AddingContract.View, View.OnClickListener,
    EasyPermissions.PermissionCallbacks {

    @Inject
    lateinit var presenter: AddingContract.Presenter

    private var _binding: FragmentAddingBinding? = null
    private val binding get() = _binding!!
    private lateinit var callback: ListContract.Callback
    private var currentPhotoPath: String? = null
    private var originalPath: String? = null

    private var loadingDialogFragment: LoadingDialogFragment? = null

    private var adapter: NumbersAdapter? = null
    private var currentAdv: Adv = Adv()
    private var file: File? = null

    fun newInstance(): AddingFragment {
        return AddingFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependency()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ListContract.Callback)
            callback = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddingBinding.inflate(inflater, container, false)
        val view = binding.root

        val toolbar = binding.toolbar
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { callback.onNavigationClick() }

        binding.switchers.initialCheckedIndex = 0

        binding.color1.setOnClickListener(this)
        binding.color2.setOnClickListener(this)
        binding.color3.setOnClickListener(this)
        binding.color4.setOnClickListener(this)
        binding.color5.setOnClickListener(this)
        binding.color6.setOnClickListener(this)

        binding.sendBtn.setOnClickListener(this)

        binding.switcherText.setOnClickListener {
            currentAdv.advType = "text"
            binding.advTextInputLayout.visibility = View.VISIBLE
            binding.colors.visibility = View.VISIBLE

            binding.imageView.visibility = View.GONE
            binding.imageAddBtn.visibility = View.GONE

        }

        binding.switcherImage.setOnClickListener {
            currentAdv.advType = "picture"
            binding.advTextInputLayout.visibility = View.GONE
            binding.colors.visibility = View.GONE

            binding.imageView.visibility = View.VISIBLE
            binding.imageAddBtn.visibility = View.VISIBLE
        }

        var b = false
        binding.advEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s!!.length == 70) {
                    if (!b)
                        Toast.makeText(
                            context,
                            "Досягнуто максимальну кількість символів",
                            Toast.LENGTH_SHORT
                        ).show()
                    b = true
                }
                if (s.length < 70)
                    b = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        binding.imageAddBtn.setOnClickListener {
            if (!EasyPermissions.hasPermissions(
                    context!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access Storage.",
                    Constants.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } else {
                ImagePickDialogFragment().show(fragmentManager!!, "ImagePickDialog")
            }
        }

        binding.addNumberBtn.setOnClickListener {
            val numberDialog = NumbersDialogFragment()
            numberDialog.show(fragmentManager!!, "NumberDialog")
        }

        adapter = NumbersAdapter(object : NumbersAdapter.OnItemClickListener {
            override fun providerClick(provider: String?, numbers: MutableList<String>?) {
            }

            override fun deleteClick(position: Int) {
                adapter?.remove(position)
            }

            override fun showNumberDialog(
                adapterPosition: Int,
                callProviders: MutableList<String>?,
                number: String?
            ) {
                ProvidersDialogFragment(
                    number,
                    adapterPosition,
                    callProviders
                ).show(fragmentManager!!, "NumberEditDialog")
            }
        })

        val layoutManager = LinearLayoutManager(context)
        binding.numbers.layoutManager = layoutManager
        binding.numbers.setHasFixedSize(true)
        binding.numbers.isNestedScrollingEnabled = false
        binding.numbers.adapter = adapter

        loadingDialogFragment = LoadingDialogFragment(R.layout.loading_dialog)

        return view
    }

    private fun isNetworkConnected(): Boolean {
        val cm =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val ni: NetworkInfo? = cm.activeNetworkInfo
                if (ni != null) {
                    return ni.isConnected && (ni.type === ConnectivityManager.TYPE_WIFI || ni.type === ConnectivityManager.TYPE_MOBILE)
                }
            } else {
                val n: Network? = cm.activeNetwork
                if (n != null) {
                    val nc: NetworkCapabilities = cm.getNetworkCapabilities(n)
                    return nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    )
                }
            }
        }
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach(this)
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unsubscribe()
    }


    private fun injectDependency() {
        val addingComponent = DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule())
            .build()

        addingComponent.inject(this)
    }

    private fun initView() {
    }

    companion object {
        const val TAG: String = "AddingFragment"
    }

    fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(context!!.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ignored: IOException) {
            }
            if (photoFile != null) {
                val photoURI =
                    FileProvider.getUriForFile(
                        context!!,
                        "com.greenzeal.voleg.fileprovider",
                        photoFile
                    )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    fun chooseGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.upload_new_image)),
            REQUEST_GALLERY_PHOTO
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                val file = File(currentPhotoPath)
                originalPath = file.absolutePath
                val bitmap: Bitmap = BitmapUtils.decodeImageFromFiles(file.absolutePath)
                val byteArrayOutputStream =
                    ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                try {
                    FileOutputStream(file).use { fileOutputStream ->
                        fileOutputStream.write(byteArrayOutputStream.toByteArray())
                        fileOutputStream.flush()
                        addItem(file)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            REQUEST_GALLERY_PHOTO ->
                if (resultCode == Activity.RESULT_OK)
                    getImageFromGallery(data)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? =
            context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.color1 -> changeColor(0)
            R.id.color2 -> changeColor(1)
            R.id.color3 -> changeColor(2)
            R.id.color4 -> changeColor(3)
            R.id.color5 -> changeColor(4)
            R.id.color6 -> changeColor(5)
            R.id.send_btn -> submitAdv()
        }
    }

    private fun submitAdv() {
        if (!isNetworkConnected()) {
            Toast.makeText(context, getString(R.string.alert_network), Toast.LENGTH_SHORT).show()
            return
        }

        currentAdv.advText = binding.advEditText.text.toString()

        var hexColor: String? = null

        if (binding.advTextInputLayout.background != null) {
            val color: ColorDrawable = binding.advTextInputLayout.background as ColorDrawable
            hexColor = java.lang.String.format("%06X", 0xFFFFFF and color.color)
        }

        currentAdv.backgroundColor = hexColor

        if (adapter?.itemCount!! > 0)
            currentAdv.advData = AdvData(adapter?.getList(), 0)

        if (!isAdvValid(currentAdv)) {
            return
        }

        val acceptParcelsDialog = AcceptParcelsDialog()
        acceptParcelsDialog.show(fragmentManager!!, "accept_parcels_dialog")

        if (currentPhotoPath != null)
            file = File(currentPhotoPath!!)
    }

    private fun isAdvValid(adv: Adv): Boolean {
        if (adv.advType == "text") {
            if (adv.advText == null) {
                Toast.makeText(context, getString(R.string.error_with_text), Toast.LENGTH_SHORT)
                    .show()
                return false
            }

            if (adv.advText!!.length <= 30) {
                binding.advEditText.error = getString(R.string.adv_text_too_short)
                binding.advEditText.requestFocus()
                val imm: InputMethodManager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.advEditText, InputMethodManager.SHOW_IMPLICIT)
                return false
            }
        } else if (adv.advType == "picture") {
            if (currentPhotoPath == null) {
                Toast.makeText(
                    context,
                    getString(R.string.adv_image_not_selected),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }

        if (adv.advData == null || adv.advData is String) {
            Toast.makeText(context, getString(R.string.no_numbers_added), Toast.LENGTH_SHORT).show()
            return false
        }

        val phoneNumbers: List<PhoneNumber>? = (adv.advData as AdvData).phoneNumbers

        if (phoneNumbers?.size == 0) {
            Toast.makeText(context, getString(R.string.no_numbers_added), Toast.LENGTH_SHORT).show()
            return false
        }

        if (checkProviders(phoneNumbers!!).isNotEmpty()) {
            return false
        }

        if (checkPhoneNumberFormats(phoneNumbers).isNotEmpty()) {
            return false
        }

        return true
    }

    private fun checkProviders(phoneNumbers: List<PhoneNumber>): MutableList<Int> {
        val errorIndexes = mutableListOf<Int>()

        for (index in phoneNumbers.indices) {
            if (phoneNumbers[index].callProviders?.size == 0) {
                errorIndexes.add(index)
                binding.numbers.findViewHolderForLayoutPosition(index)?.itemView?.findViewById<EditText>(
                    R.id.number_edit_text
                )!!.error =
                    getString(R.string.adve_no_call_providers_for_phone)
                binding.numbers.findViewHolderForLayoutPosition(index)?.itemView?.findViewById<EditText>(
                    R.id.number_edit_text
                )!!
                    .requestFocus()
            }

        }
        return errorIndexes
    }

    private fun checkPhoneNumberFormats(phoneNumbers: List<PhoneNumber>): MutableList<Int> {
        val errorIndexes = mutableListOf<Int>()

        for (index in phoneNumbers.indices) {
            if (!isValidMobile(phoneNumbers[index].number?.replace(Constants.PREFIX, ""))) {
                binding.numbers.findViewHolderForLayoutPosition(index)?.itemView?.findViewById<EditText>(
                    R.id.number_edit_text
                )!!.error =
                    getString(R.string.phone_numbers_are_not_valid)
                binding.numbers.findViewHolderForLayoutPosition(index)?.itemView?.findViewById<EditText>(
                    R.id.number_edit_text
                )!!
                    .requestFocus()
                errorIndexes.add(index)
            }
        }
        return errorIndexes
    }

    private fun changeColor(i: Int) {
        val color = arrayOf(0xFFFEFFBF, 0xFFFCDCDF, 0xFF79AAAD, 0xFFC997C6, 0xFFB0EFEF, 0xFF83D9DC)
        binding.advTextInputLayout.setBackgroundColor(color[i].toInt())
    }

    override fun addItem(file: File?) {
        currentPhotoPath = file?.absolutePath
        binding.imageViewPlaceholder.visibility = View.GONE
        Glide.with(this)
            .load(file)
            .into(binding.imageToUpload)

        val loadingDialogFragment = LoadingDialogFragment(R.layout.recognition_dialog)
        loadingDialogFragment.show(fragmentManager!!, "RecognitionDialog")

        val image: InputImage =
            InputImage.fromBitmap(BitmapFactory.decodeFile(originalPath), 0)

        val recognizer = TextRecognition.getClient()
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val pattern =
                    Pattern.compile("(\\+)?(\\(\\d{2,3}\\) ?\\d|\\d)(([ \\-]?\\d)|( ?\\(\\d{2,3}\\) ?)){5,12}\\d")
                val matcher: Matcher = pattern.matcher(visionText.text)
                while (matcher.find()) {
                    val start = matcher.start()
                    val end = matcher.end()
                    addNumber(visionText.text.substring(start,end), mutableListOf("Mobile"))
                }
                loadingDialogFragment.dismiss()
            }
            .addOnFailureListener { e ->
                loadingDialogFragment.dismiss()
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnCanceledListener {
                loadingDialogFragment.dismiss()
            }
    }

    override fun showProgress(b: Boolean) {
        if (b) {
            loadingDialogFragment!!.show(fragmentManager!!, "LoadingDialog")
        } else {
            loadingDialogFragment!!.dismiss()
        }
    }

    override fun showErrorMessage(message: String?) {
        Toast.makeText(context, message ?: "Error", Toast.LENGTH_LONG).show()
    }

    override fun loadAdvSuccess() {
        ConfirmDialogFragment(getString(R.string.thanks_for_submitting_adv), true).show(
            fragmentManager!!,
            "ConfirmDialog"
        )
    }

    private var str =
        "^\\s?((\\+[1-9]{1,4}[ \\-]*)|(\\([0-9]{2,3}\\)[ \\-]*)|([0-9]{2,4})[ \\-]*)*?[0-9]{3,4}?[ \\-]*[0-9]{3,4}?\\s?"

    private fun isValidMobile(phone: String?): Boolean {
        return if (phone != null)
            Pattern.compile(str).matcher(phone).matches()
        else false
    }

    private fun getImageFromGallery(data: Intent?) {
        if (data == null) return
        if (data.clipData != null) {
            val count = data.clipData!!.itemCount
            for (i in 0 until count) {
                saveFileInStorage(data.clipData!!.getItemAt(i).uri)
            }
        } else if (data.data != null) {
            saveFileInStorage(data.data)
        }
    }

    private fun saveFileInStorage(uri: Uri?) {
        Thread {
            val file: File
            try {
                val mimeType =
                    context?.contentResolver!!.getType(uri!!)
                if (mimeType != null) {
                    val inputStream =
                        context?.contentResolver!!.openInputStream(uri)
                    val fileName = getFileName(uri)
                    if (fileName != "") {
                        file = File(
                            context?.getExternalFilesDir(
                                Environment.DIRECTORY_DOWNLOADS
                            )!!.absolutePath + "/" + fileName
                        )
                        originalPath = file.absolutePath
                        val bitmap: Bitmap = BitmapUtils.decodeImageFromFiles(inputStream)
                        inputStream!!.close()
                        val byteArrayOutputStream =
                            ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                        FileOutputStream(file).use { fileOutputStream ->
                            fileOutputStream.write(byteArrayOutputStream.toByteArray())
                            fileOutputStream.flush()
                            Handler(Looper.getMainLooper()).post {
                                addItem(
                                    file
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun getFileName(uri: Uri?): String {
        var displayName = ""
        val cursor: Cursor? = context?.contentResolver!!
            .query(uri!!, null, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return displayName
    }

    fun addNumber(number: String?, providers: MutableList<String>) {
        adapter?.add(PhoneNumber(number, providers))
        binding.numbers.requestLayout()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        ImagePickDialogFragment().show(fragmentManager!!, "ImagePickDialog")
    }

    fun updateProvider(
        number: String?,
        selectedItems: MutableList<String>,
        adapterPosition: Int
    ) {
        adapter?.updateProvider(number, adapterPosition, selectedItems)
    }

    fun setAcceptParcels(accept: Int) {
        val phoneNumbers: List<PhoneNumber>? = (currentAdv.advData as AdvData).phoneNumbers
        if (phoneNumbers != null) {
            for (item in phoneNumbers) {
                item.number = Constants.PREFIX + item.number
                for (provider in item.callProviders!!.indices) {
                    item.callProviders!![provider] = item.callProviders!![provider].toLowerCase(
                        Locale.ROOT
                    ).replace(" ", "")
                }
            }
        }

        if (currentAdv.advData != null){
            if(currentAdv.advData is AdvData)
                (currentAdv.advData as AdvData).acceptParcels = accept
        }
        presenter.submitAdv(currentAdv, file)
    }
}