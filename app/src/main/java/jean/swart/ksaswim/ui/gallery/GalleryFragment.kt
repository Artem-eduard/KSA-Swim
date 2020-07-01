package jean.swart.ksaswim.ui.gallery

//import com.itextpdf.awt.geom.Rectangle

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import jean.swart.ksaswim.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel
    private val CAMERA_CERT_REQUEST = 1337
    private val CAMERA_CHILD_REQUEST = 1888
    var photoOfChild: Bitmap? = null
    var photoOfCert: Bitmap? = null


    private val MY_CAMERA_PERMISSION_CODE = 100

    fun sendEmail(f: File) {
        try {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "text/plain"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("knysna.aquatics@gmail.com"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "2020 KSA Swim Contract")
            if (f!=null)
            {
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f))
            }
            emailIntent.putExtra(Intent.EXTRA_TEXT, "a")
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."))
        } catch (t: Throwable) {

        }
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val b: Button = root.findViewById(R.id.button2) as Button
        val b2: Button = root.findViewById(R.id.button4) as Button
        val bsubmit:Button = root.findViewById(R.id.button) as Button
        val firstname:EditText = root.findViewById(R.id.pupilFirstnameEditText) as EditText
        val surname:EditText = root.findViewById(R.id.PupilSurnameEditText) as EditText
        val nickname:EditText = root.findViewById(R.id.PupilNicknameEditText) as EditText
        val idno:EditText = root.findViewById(R.id.IDEditText) as EditText
        val dob:EditText = root.findViewById(R.id.DOBEditText) as EditText
        val mothername:EditText = root.findViewById(R.id.MotherNameEditText) as EditText
        val motheremail:EditText = root.findViewById(R.id.MotherEmailEditText) as EditText
        val motherphonework:EditText = root.findViewById(R.id.MotherPhoneworkEditText) as EditText
        val motherphonecell:EditText = root.findViewById(R.id.MotherPhonecellEditText) as EditText
        val fathername:EditText = root.findViewById(R.id.FatherNameEditText) as EditText
        val fatheremail:EditText = root.findViewById(R.id.FatherEmailEditText) as EditText
        val fatherphonework:EditText = root.findViewById(R.id.FatherPhoneworkEditText) as EditText
        val fatherphonecell:EditText = root.findViewById(R.id.FatherPhonecellEditText) as EditText
        val phyaddress:EditText = root.findViewById(R.id.PhysicalAddressEditText) as EditText
        val postaddress:EditText = root.findViewById(R.id.PostalAddressEditText) as EditText
        val personres:EditText = root.findViewById(R.id.PersonResEditText) as EditText
        val personresid:EditText = root.findViewById(R.id.PersonResIDEditText) as EditText
        val withwhom:EditText = root.findViewById(R.id.PastLessonEditText) as EditText
        val badexp:EditText = root.findViewById(R.id.BadexpEditText) as EditText
        val other:EditText = root.findViewById(R.id.otherEditText) as EditText

        b.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            startActivityForResult(cameraIntent, CAMERA_CHILD_REQUEST)
        }

        b2.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            startActivityForResult(cameraIntent, CAMERA_CERT_REQUEST)
        }

        bsubmit.setOnClickListener {

            val dateString = dob.text.toString()
            val baos = ByteArrayOutputStream()
            val reader = PdfReader(resources.openRawResource(R.raw.contract))
            val stamper = PdfStamper(reader, baos)
            val form = stamper.acroFields
            if (dateString.length>0) {
                val sdf = SimpleDateFormat("yyyy/MM/dd")
                val date: Date = sdf.parse(dateString)

                val outputFormat = SimpleDateFormat("yyyy")
                val year: String = outputFormat.format(date)
                val month: String = SimpleDateFormat("MM").format(date)
                val day: String = SimpleDateFormat("dd").format(date)
                form.setField("Year",year)
                form.setField("Month", month)
                form.setField("Date", day)
            }



            form.setField("firstname", firstname.text.toString())
            form.setField("surname", surname.text.toString())
            form.setField("nickname", nickname.text.toString())
            form.setField("idnumber", idno.text.toString())

            form.setField("mother_name", mothername.text.toString())
            form.setField("mother_email", motheremail.text.toString())
            form.setField("mother_phone_work", motherphonework.text.toString())
            form.setField("mother_phone_cell", motherphonecell.text.toString())
            form.setField("father_name", fathername.text.toString())
            form.setField("father_email", fatheremail.text.toString())
            form.setField("father_phone_work", fatherphonework.text.toString())
            form.setField("father_phone_cell", fatherphonecell.text.toString())
            form.setField("physical_address", phyaddress.text.toString())
            form.setField("postal_address", postaddress.text.toString())
            form.setField("person_responsible", personres.text.toString())
            form.setField("idnumber_responsible", personresid.text.toString())
            form.setField("with_whom", withwhom.text.toString())
            form.setField("bad_experience", badexp.text.toString())
            form.setField("other", other.text.toString())

            val rect = form.getFieldPositions("child_image")[0].position
            val page = form.getFieldPositions("child_image")[0].page
            val stream3 = ByteArrayOutputStream()
            if (photoOfChild!=null) {
                photoOfChild?.compress(Bitmap.CompressFormat.PNG, 100, stream3)
                val maimg: Image = Image.getInstance(stream3.toByteArray())

                maimg.setAbsolutePosition(22f, 690f)
                maimg.scaleAbsolute(rect.width, rect.height)

                stamper.getOverContent(page).addImage(maimg)
            }

            val rect2 = form.getFieldPositions("birth_image_af_image")[0].position
            val page2 = form.getFieldPositions("birth_image_af_image")[0].page
            val stream4 = ByteArrayOutputStream()
            if (photoOfCert!=null) {
                photoOfCert?.compress(Bitmap.CompressFormat.PNG, 100, stream4)
                val maimg2 = Image.getInstance(stream4.toByteArray())

                maimg2.setAbsolutePosition(20f, 20f)
                maimg2.scaleAbsolute(rect2.width, rect2.height)

                stamper.getOverContent(page2).addImage(maimg2)
            }
        //    form.setField("child_image", photoOfChild)
        //    form.setField("birth_image_af_image", photoOfCert)
            stamper.setFormFlattening(true)
            stamper.close()
            reader.close()


            try {
                val rootPath: String = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/"
                val root = File(rootPath)
                if (!root.exists()) {
                    root.mkdirs()
                }
                val f = File(rootPath + "/contract2.pdf")
                if (f.exists()) {
                    f.delete()
                }
                f.createNewFile()
                val out = FileOutputStream(f)
                baos.writeTo(out)
                sendEmail(f)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val textView: TextView = root.findViewById(R.id.text_gallery)

        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })



        return root
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
       if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_CHILD_REQUEST) {
                photoOfChild = data?.extras!!["data"] as Bitmap?
            }
            if (requestCode == CAMERA_CERT_REQUEST) {
                photoOfCert = data?.extras!!["data"] as Bitmap?
            }
        }
    }




}


/* This is the source to send email directly without external app. not sure why error
               Thread(Runnable {
                   try {
                       val props = Properties()
                       props.put("mail.smtp.auth", "true")
                       props.put("mail.smtp.starttls.enable", "true")
                       props.put("mail.smtp.host", "smtp.gmail.com")
                       props.put("mail.smtp.port", "587")
                       val username="artem.beletskii2019"
                       val password="Ryongak75.8995#"
                       val session: Session = Session.getInstance(props,
                               object : Authenticator() {
                                   override fun getPasswordAuthentication(): PasswordAuthentication {
                                       return PasswordAuthentication(
                                               username, password)
                                   }
                               })
                       // TODO Auto-generated method stub


                       val message =MimeMessage(session)

                       message.setFrom(InternetAddress("artem.beletskii2019@gmail.com"))
                       message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse("valeriya.gras@outlook.com"))
                       message.setSubject("email")
                       message.setText("""HI,great""")
                       Transport.send(message)
                       println("Done")
                   } catch (e: MessagingException) {
                       throw RuntimeException(e)
                   }
               }).start()
*/