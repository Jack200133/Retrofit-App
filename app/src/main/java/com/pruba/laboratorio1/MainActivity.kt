package com.pruba.laboratorio1

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.pruba.laboratorio1.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArticleAdapter
    private val articleList = mutableListOf<Articles>()
    var piaz:String = "us"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchNews.setOnQueryTextListener(this)
        initRecycler()
        searchNew("general")

        val paices = resources.getStringArray(R.array.contris)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, paices)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        auto_complete_text_view.threshold = 1


        // Set an item click listener for auto complete text view
        auto_complete_text_view.onItemClickListener = AdapterView.OnItemClickListener{
            parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString().toLowerCase()
            // Display the clicked item using toast
            searchNewCon(selectedItem)
            piaz = selectedItem
            Toast.makeText(applicationContext,"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }



    }

    private fun searchNewCon(country: String){
        val api = Retrofit2()
        CoroutineScope(Dispatchers.IO).launch {

            val call = api.getService()?.getNewsByCategory(
                    country,
                    "general",
                    "96d1a256fdf14667964b77f96059b578"
            )
            val news: NewsResponse? = call?.body()

            runOnUiThread{
                if(call!!.isSuccessful){
                    if(news?.status.equals("ok")){
                        val articles = news?.articles?: emptyList()
                        articleList.clear()
                        articleList.addAll(articles)
                        adapter.notifyDataSetChanged()
                    }else{
                        showMessage("Error en webservice")
                    }

                }else{
                    showMessage("Error en retrofit")
                }
            }
        }
    }



    private fun initRecycler(){

        adapter = ArticleAdapter(articleList)
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.adapter = adapter


    }

    private fun searchNew(category: String){
        val api = Retrofit2()
        CoroutineScope(Dispatchers.IO).launch {

            val call = api.getService()?.getNewsByCategory(
                piaz,
                category,
                "4b94054dbc6b4b3b9e50d8f62cde4f6c"
            )
            val news: NewsResponse? = call?.body()

            runOnUiThread{
                if(call!!.isSuccessful){
                    if(news?.status.equals("ok")){
                        val articles = news?.articles?: emptyList()
                        articleList.clear()
                        articleList.addAll(articles)
                        adapter.notifyDataSetChanged()
                    }else{
                        showMessage("Error en webservice")
                    }

                }else{
                    showMessage("Error en retrofit")
                }
            }
        }
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
    }


    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun buscarP(query: String?): Boolean{
        if (!query.isNullOrEmpty()){
            searchNew(query.toLowerCase(Locale.ROOT))
        }
        return true
    }
    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()){
            searchNew(query.toLowerCase(Locale.ROOT))
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}