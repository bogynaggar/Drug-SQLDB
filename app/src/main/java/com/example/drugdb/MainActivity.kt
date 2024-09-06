package com.example.drugdb

//noinspection SuspiciousImport
import android.R
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drugdb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var  selectedCategory :String
    // var isDrugCatSelected = false
    val defaultTextForSpinner = "Please Select"
    var drugCat : MutableList<String> =  ArrayList()
    var drugArrayList: MutableList<Drug> = ArrayList<Drug>()
    val sql = MySQL(this)


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ///////////// initialize values first time  ///////////////////

        setInitialValues()

        /////////////  SQL //////////

        drugCat = sql.readCATData()
        drugArrayList = sql.readData()

        ////////////  Recycle View /////////////

        loadRV(drugArrayList)
        binding.EDId.setText((drugArrayList.size+1).toString())

        ////////////  Spinner /////////////

        loadSpinner()

        ////////////  Buttons //////////////

        binding.btClear.setOnClickListener {

            clear()
        }

        binding.btInsert.setOnClickListener{

            val id = drugArrayList.size + 1
            val name = binding.EDDrugName.text
            val aI = binding.EDDrugAIng.text.toString()
            val cat = binding.SpDrugCategory.selectedItem.toString()
            val price = binding.EDDrugPrice.text.toString()

            val drug = Drug(id , name.toString(), aI , cat , price)
            if (name.isEmpty()){
                Toast.makeText(this , "FAILED As empty Fields" ,Toast.LENGTH_SHORT).show()
            }else {
                sql.insertData(drug)
            }
            drugArrayList = sql.readData()
            loadRV(drugArrayList)
        }

        binding.btUpdate.setOnClickListener {

            val id =  binding.EDId.text.toString().toInt()
            val name = binding.EDDrugName.text.toString()
            val aI = binding.EDDrugAIng.text.toString()
            val cat = binding.SpDrugCategory.selectedItem.toString()
            val price = binding.EDDrugPrice.text.toString()

            val drug = Drug(id , name , aI , cat , price)
            if (sql.updateData(drug , id.toString() )){
                Toast.makeText(this , "Updated Successfully" ,Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this , "FAILED to Update" ,Toast.LENGTH_SHORT).show()
            }
            drugArrayList = sql.readData()
            loadRV(drugArrayList)

        }

        binding.btDelete.setOnClickListener {
            val id =  binding.EDId.text.toString().toInt()
            sql.deleteSelected(id)
            drugArrayList = sql.readData()
            loadRV(drugArrayList)
            clear()
            val lastID = drugArrayList[drugArrayList.size-1].id+1
            binding.EDId.setText((lastID).toString())


        }

        binding.btDeleteAll.setOnClickListener {

            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            sql.deleteALLData()
                            drugArrayList = sql.readData()
                            binding.EDId.setText((drugArrayList.size+1).toString())
                            loadRV(drugArrayList)
                            Toast.makeText(this, "All Database deleted Successfully", Toast.LENGTH_SHORT).show()
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure? All Database will be Deleted permanently").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()

            clear()
        }

        binding.EDDrugName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                loadRV(drugArrayList)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val drugList = sql.search(s)
                loadRV(drugList)
           //     binding.EDId.setText((drugArrayList.size+1).toString())

            }
        })


    }


    private fun loadRV (drugList: MutableList<Drug> ){

        val itemRV  = binding.RV
        val itemAdapter = RVAdapter( drugList)
        itemRV.layoutManager = LinearLayoutManager(this)
        itemRV.adapter = itemAdapter
        itemRV.setHasFixedSize(true)
        itemAdapter.setOnClickListener(object :
            RVAdapter.OnClickListener {
            override fun onClick(position: Int, model: Drug) {
                binding.EDId.setText(model.id.toString())
                binding.EDDrugName.setText(model.name)
                binding.EDDrugAIng.setText(model.activeI)

                val selector = drugCat.indexOf(model.category)
                Toast.makeText(this@MainActivity, "Selected Category is " + model.category, Toast.LENGTH_SHORT).show()
                binding.SpDrugCategory.setSelection(selector)
                selectedCategory = drugCat[selector]

                binding.EDDrugPrice.setText(model.price)
            }
        })

    }

    private fun loadSpinner(){

        val spinner =   binding.SpDrugCategory
        val adapter = ArrayAdapter(this ,R.layout.simple_spinner_item, drugCat)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {

                if (parent.id == spinner.id ){
                    if (spinner.selectedItem.toString() == defaultTextForSpinner){
                        //   isDrugCatSelected = false
                        //   Toast.makeText(this@MainActivity, "Selected item "  + "is " + drugCat[position], Toast.LENGTH_SHORT).show()

                    }else{
                        selectedCategory = drugCat[position]
                       //   isDrugCatSelected = true
                        Toast.makeText(this@MainActivity, "Selected Category "  + "is " + drugCat[position], Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            } }
    }

    private fun setInitialValues () {
        val sharedPref :SharedPreferences  = getSharedPreferences("Settings" ,MODE_PRIVATE )
        val  token = sharedPref.getString("firstLoad", "false")
        if (token == "false") {
            val drugCat : MutableList<String> =  ArrayList()
            drugCat.addAll(listOf("Please Select" , "Antibiotic" , "AntiInflammatory" , "AntiHyperTension" , "Diuretic"))
            for (i in 0..< drugCat.size){
                sql.insertCATData(drugCat[i])
            }

            val drugArrayList: MutableList<Drug> = ArrayList<Drug>()
            drugArrayList.add(Drug(1, "Flumox 500 mg 16 cap" , "Amoxicillin + Floxacillin " , "Antibiotic" , "71.0"))
            drugArrayList.add(Drug(2, "Cetal 500 mg 20tab" , "Paracetamol" , "AntiInflammatory" , "24.0"))
            drugArrayList.add(Drug(3, "Concor 5 mg 30 F.C tab" , "Bisoprolol" , "AntiHyperTension" , "72.0"))
            drugArrayList.add(Drug(4, "Edemex 1mg 20 tab" , "Bumetanide" , "Diuretic" , "18.0"))
            drugArrayList.add(Drug(5, "Cataflam 50 mg 20 tab" , "Diclofenac Potassium" , "AntiInflammatory" , "68.0"))
            drugArrayList.add(Drug(6, "Tavanic 500mg 5 tab" , "Levofloxacin" , "Antibiotic" , "134.0"))
            drugArrayList.add(Drug(7, "Augmentin 1gm 14 tab" , "Amoxicillin + claulonic acid" , "Antibiotic" , "131.0"))

            for (i in 0..< drugArrayList.size){
                sql.insertData(drugArrayList[i])
            }
            val editor = sharedPref.edit();
            editor.putString("firstLoad", "true");
            editor.apply();

        }


    }

    @SuppressLint("SetTextI18n")
    private fun clear (){
        binding.EDDrugName.text = null
        binding.EDDrugAIng.text = null
        binding.SpDrugCategory.setSelection(0)
        binding.EDDrugPrice.text = null
        binding.EDId.setText((drugArrayList.size+1).toString())

    }

}

