package br.com.jpfurlan.controleatividadesepomodoro;

import static br.com.jpfurlan.controleatividadesepomodoro.ListagemAtividades.TIPO;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import br.com.jpfurlan.controleatividadesepomodoro.persistencia.AtividadeDAO;
import br.com.jpfurlan.controleatividadesepomodoro.persistencia.AtividadesDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String ARQUIVO = "br.com.jpfurlan.sharedpreferences.SUGESTAO_CAMPOS";

    public static final String MODO    = "MODO";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;
    public static final String NOME = "NOME";
    public String nomeOpcao = "";
    public static final String PRIORIDADE = "PRIORIDADE";
    public static final String STATUS = "STATUS";
    public static final String COMPLEXIDADE = "COMPLEXIDADE";
    public static final String TIPOS = "TIPOS";
    private static EditText editTextAtividade;
    private static CheckBox cbPrioridade;
    private int    modo;
    private RadioGroup radioGroupColunas, radioGroupComplexidade;
    private Spinner spinnerTipo;
    private String nomeOriginal;

    private AtividadesDatabase atividadesDatabase;
    private AtividadeDAO atividadeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        atividadesDatabase = AtividadesDatabase.getDatabase(this);
        atividadeDAO = atividadesDatabase.atividadeDAO();

        editTextAtividade  = findViewById(R.id.editTextNomeAtividade);

        cbPrioridade = findViewById(R.id.checkBoxPrioridade);

        radioGroupColunas = findViewById(R.id.radioGroupColunas);

        radioGroupComplexidade = findViewById(R.id.radioGroupComplexidade);

        spinnerTipo = findViewById(R.id.spinnerTipo);
        popularSpinner();
        Intent intent = getIntent();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            modo = bundle.getInt(MODO, NOVO);
            if (modo == NOVO) {
                setTitle(getString(R.string.nova_atividade));
            } else {
                nomeOriginal = bundle.getString(NOME);
                editTextAtividade.setText(nomeOriginal);

                cbPrioridade.setChecked(Boolean.parseBoolean(bundle.getString(PRIORIDADE)));

                String status = bundle.getString(STATUS);
                for (int i = 0; i < radioGroupColunas.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) radioGroupColunas.getChildAt(i);
                    if (radioButton.getText().toString().equals(status)) {
                        radioButton.setChecked(true);
                        break;
                    }
                }

                String complexidade = bundle.getString(COMPLEXIDADE);
                for (int i = 0; i < radioGroupComplexidade.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) radioGroupComplexidade.getChildAt(i);
                    if (radioButton.getText().toString().equals(complexidade)) {
                        radioButton.setChecked(true);
                        break;
                    }
                }

                String tipo = bundle.getString(TIPOS);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerTipo.getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).equals(tipo)) {
                        spinnerTipo.setSelection(i);
                        break;
                    }
                }

                setTitle(getString(R.string.alterar_atividade));
            }
        }

        lerPreferenciasCampos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.atividade_opcoes, menu);
        return true;
    }

    private void lerPreferenciasCampos() {
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);

        nomeOpcao = shared.getString(NOME, nomeOpcao);
        cbPrioridade.setChecked(shared.getBoolean("prioridadeAtividade", false));

        int selectedStatusId = shared.getInt("selectedStatusId", -1);
        if(selectedStatusId != -1) {
            radioGroupColunas.check(selectedStatusId);
        }

        int selectedComplexidadeId = shared.getInt("selectedComplexidadeId", -1);
        if(selectedComplexidadeId != -1) {
            radioGroupComplexidade.check(selectedComplexidadeId);
        }

        spinnerTipo.setSelection(shared.getInt("selectedTipoIndex", 0));
    }

    private void salvarPreferenciasCampos() {
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();

        editor.putString(NOME, editTextAtividade.getText().toString());
        editor.putBoolean("prioridadeAtividade", cbPrioridade.isChecked());
        editor.putInt("selectedStatusId", radioGroupColunas.getCheckedRadioButtonId());
        editor.putInt("selectedComplexidadeId", radioGroupComplexidade.getCheckedRadioButtonId());
        editor.putInt("selectedTipoIndex", spinnerTipo.getSelectedItemPosition());
        editor.commit();
    }


    public void limparCampos(View view) {
        editTextAtividade.setText(null);
        cbPrioridade.setChecked(false);
        radioGroupColunas.clearCheck();
        radioGroupComplexidade.clearCheck();
        spinnerTipo.setSelection(0);

        Toast.makeText(this, R.string.campos_limpos, Toast.LENGTH_SHORT).show();
    }

    public void salvar(View view) {
        String nomeAtividade = editTextAtividade.getText().toString();
        if (nomeAtividade == null || nomeAtividade.trim().isEmpty()) {
            Toast.makeText(this, R.string.erro_nome, Toast.LENGTH_SHORT).show();
            editTextAtividade.requestFocus();
            return;
        }

        String prioridadeAtividade = String.valueOf(cbPrioridade.isChecked());

        int selectedStatusId = radioGroupColunas.getCheckedRadioButtonId();
        RadioButton selectedStatusButton = findViewById(selectedStatusId);
        if (selectedStatusButton == null) {
            Toast.makeText(this, R.string.por_favor_selecione_um_status, Toast.LENGTH_SHORT).show();
            radioGroupColunas.requestFocus();
            return;
        }
        String statusAtividade = selectedStatusButton.getText().toString();

        int selectedComplexidadeId = radioGroupComplexidade.getCheckedRadioButtonId();
        RadioButton selectedComplexidadeButton = findViewById(selectedComplexidadeId);
        if (selectedComplexidadeButton == null) {
            Toast.makeText(this, R.string.entre_com_um_valor_de_complexidade, Toast.LENGTH_SHORT).show();
            radioGroupComplexidade.requestFocus();
            return;
        }
        String complexidadeAtividade = selectedComplexidadeButton.getText().toString();

        String tiposAtividade = spinnerTipo.getSelectedItem().toString();
        if (tiposAtividade.trim().isEmpty()) {
            spinnerTipo.requestFocus();
            Toast.makeText(this, R.string.entre_com_um_valor_de_tipo, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.salvo_com_sucesso, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.putExtra(NOME, nomeAtividade);
        intent.putExtra(PRIORIDADE, prioridadeAtividade);
        intent.putExtra(STATUS, statusAtividade);
        intent.putExtra(COMPLEXIDADE, complexidadeAtividade);
        intent.putExtra(TIPOS, tiposAtividade);

        setResult(Activity.RESULT_OK, intent);

        salvarPreferenciasCampos();

        finish();
    }

    private void popularSpinner() {
        ArrayList<String> lista = new ArrayList<>();
        lista.add("");
        lista.add(getString(R.string.lazer));
        lista.add(getString(R.string.esporte));
        lista.add(getString(R.string.trabalho));
        lista.add(getString(R.string.pessoal));
        lista.add(getString(R.string.outro));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);

        spinnerTipo.setAdapter(adapter);
    }

    public static void novaAtividade(AppCompatActivity activity){
        Intent intent = new Intent(activity, MainActivity.class);

        intent.putExtra(NOME, NOVO);

        activity.startActivityForResult(intent, NOVO);
    }

    public static void alterarAtividade(Activity activity, Atividade atividade, int requestCode){
        Intent intent = new Intent(activity, MainActivity.class);

        intent.putExtra(MODO, requestCode);
        intent.putExtra(NOME, atividade.getNome());
        intent.putExtra(PRIORIDADE, atividade.getPrioridade());
        intent.putExtra(STATUS, atividade.getStatus());
        intent.putExtra(COMPLEXIDADE, atividade.getComplexidade());
        intent.putExtra(TIPO, atividade.getTipo());

        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemSalvar:
                salvar(null);
                return true;

            case android.R.id.home:
                cancelar();
                return true;
            case R.id.menuItemLimpar:
                limparCampos(null);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void cancelar(View view){
        onBackPressed();
    }
}