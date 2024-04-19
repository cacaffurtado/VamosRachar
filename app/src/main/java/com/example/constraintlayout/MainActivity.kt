package com.example.constraintlayout

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.content.Intent
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var ttsSucess: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mostrarMsg()
        ativarCompartilhamento()

        // Initialize TTS engine
        tts = TextToSpeech(this, this)

    }

    fun mostrarMsg(){
        val conta : EditText = findViewById(R.id.edt_conta)
        val qtdPessoa : EditText = findViewById(R.id.edt_qtdPessoas)
        val mensagem : TextView = findViewById(R.id.mensagem)

        conta.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.d("PDM24","Valor da Conta Vazio")
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.d("PDM24","Mudando Valor da Conta")
                if (s.isNotEmpty() && conta.text.isNotEmpty() && !qtdPessoa.text.equals("0")){
                    val contaD = conta.text.toString().toDouble()
                    val qtdPessoaD = qtdPessoa.text.toString().toDouble()
                    mensagem.text = calculo(contaD, qtdPessoaD)
                } else{
                    mensagem.text = ""
                }
            }

            override fun afterTextChanged(s: Editable) {
                Log.d ("PDM24", "Valor da Conta Editado")
            }
        })

        qtdPessoa.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                Log.d("PDM24","Quantidade de Pessoas Vazio")
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.d("PDM24","Mudando Quantidade de Pessoas")
                if (conta.text.isNotEmpty() && qtdPessoa.text.equals("0") && s.isNotEmpty()){
                    val contaD = conta.text.toString().toDouble()
                    val qtdPessoaD = qtdPessoa.text.toString().toDouble()
                    mensagem.text = calculo(contaD, qtdPessoaD)
                } else{
                    mensagem.text = ""
                }
            }

            override fun afterTextChanged(s: Editable) {
                Log.d ("PDM24", "Quantidade de Pessoas Editado")
            }
        })
    }

    fun ativarCompartilhamento(){
        val btnCompartilhar : ImageButton = findViewById(R.id.btn_compartilhar)

        btnCompartilhar.setOnClickListener {
            val mensagem : TextView = findViewById(R.id.mensagem)

            if (mensagem.text.toString().isNotEmpty()){
                val enviar : Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, mensagemFinal())
                    type = "text/plain"
                }

                val compartilhar = Intent.createChooser(enviar, null)

                startActivity(compartilhar)
            }

        }
    }

    fun clickFalar(v: View){
        if (tts.isSpeaking) {
            tts.stop()
        }
        if(ttsSucess) {
            Log.d ("PDM24", tts.language.toString())
            tts.speak(mensagemFinal(), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun mensagemFinal() : String{
        //Chamando o Valor da Conta e a Quantidade de Pessoas
        val conta : TextView = findViewById(R.id.edt_conta)
        val qtdPessoa : TextView = findViewById(R.id.edt_qtdPessoas)

        if (conta.text.isNotEmpty() && qtdPessoa.text.isNotEmpty()){
            val contaT = conta.text.toString()
            val qtdPessoaT = qtdPessoa.text.toString()

            val contaIndiviual = calculo(contaT.toDouble(), qtdPessoaT.toDouble())

            return "Bom dia, CALOTEIROS! A conta deu $contaT e, dividindo para $qtdPessoaT lindes, ficou $contaIndiviual para cada um. O PIX é o meu telefone. Xêro!"
        } else {
            return "Ainda existem campos a serem preenchidos!"
        }

    }

    fun calculo(conta : Double, qtdPessoa : Double) : String{
        //Divisão da Conta
        val contaDividida = conta/qtdPessoa

        //Formatação
        val df = DecimalFormat("##.##")

        //Mensagem mostrada
        return "R$${df.format(contaDividida)} para cada!"
    }

    override fun onDestroy() {
        // Release TTS engine resources
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // TTS engine inicializado com sucesso
            tts.language = Locale.getDefault()
            ttsSucess=true
            Log.d("PDM24","Sucesso na Inicialização")
        } else {
            // TTS engine falhou ao inicializar
            Log.e("PDM24", "Falha ao inicializar TTS engine.")
            ttsSucess=false
        }
    }


}

