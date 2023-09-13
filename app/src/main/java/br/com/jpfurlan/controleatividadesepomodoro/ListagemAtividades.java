package br.com.jpfurlan.controleatividadesepomodoro;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.jpfurlan.controleatividadesepomodoro.persistencia.AtividadeDAO;
import br.com.jpfurlan.controleatividadesepomodoro.persistencia.AtividadesDatabase;
import br.com.jpfurlan.controleatividadesepomodoro.utils.UtilsGUI;

public class ListagemAtividades extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final int    NOVO    = 1;
    public static final String NOME = "NOME";
    public static final String PRIORIDADE = "PRIORIDADE";
    public static final String STATUS = "STATUS";
    public static final String COMPLEXIDADE = "COMPLEXIDADE";
    public static final String TIPO = "TIPO";
    private int        posicaoSelecionada = -1;
    private ActionMode actionMode;
    private ListView listViewAtividades;
    private AtividadeAdapter atividadeAdapter;
    private List<Atividade> listaAtividades = new ArrayList<>();
    private View       viewSelecionada;

    private AtividadeDAO atividadeDAO;

    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listViewAtividades = findViewById(R.id.listViewAtividades);
        listViewAtividades.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id) {

                        posicaoSelecionada = position;
                        alterarAtividade();
                    }
                });

        listViewAtividades.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewAtividades.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view,
                                                   int position,
                                                   long id) {

                        if (actionMode != null){
                            return false;
                        }

                        posicaoSelecionada = position;

                        view.setBackgroundColor(Color.LTGRAY);

                        viewSelecionada = view;

                        listViewAtividades.setEnabled(false);

                        actionMode = startSupportActionMode(mActionModeCallback);

                        return true;
                    }
                });
        layout = findViewById(R.id.viewInicial);
        atividadeDAO = AtividadesDatabase.getDatabase(this).atividadeDAO();
        popularListaAtividades();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal_opcoes, menu);
        return true;
    }

    private void mostrarMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menuItemAdicionar:
                novaAtividade(this);
                return true;
            case R.id.menuItemSobre:
                abrirSobre(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.principal_item_selecionado, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch(item.getItemId()){
                case R.id.menuItemAlterar:
                    alterarAtividade();
                    mode.finish();
                    return true;

                case R.id.menuItemExcluir:
                    excluirAtividade();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            if (viewSelecionada != null){
                viewSelecionada.setBackgroundColor(Color.TRANSPARENT);
            }

            actionMode         = null;
            viewSelecionada    = null;

            listViewAtividades.setEnabled(true);
        }
    };

    public static void novaAtividade(AppCompatActivity activity){

        Intent intent = new Intent(activity, MainActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, NOVO);
    }

    public static void abrirSobre(AppCompatActivity activity){

        Intent intent = new Intent(activity, SobreActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, NOVO);
    }

    private void excluirAtividade(){

        String mensagem = getString(R.string.deseja_realmente_apagar);

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:
                                try {
                                    Atividade atividade = listaAtividades.get(posicaoSelecionada);
                                    atividadeDAO.delete(atividade);
                                    listaAtividades.remove(posicaoSelecionada);
                                    atividadeAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mostrarMensagem("Erro ao excluir a atividade: " + e.getMessage());
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }


    private void alterarAtividade(){
        Atividade atividade = listaAtividades.get(posicaoSelecionada);
        MainActivity.alterarAtividade(this, atividade, MainActivity.ALTERAR);
    }

    private void popularListaAtividades() {
        listaAtividades = atividadeDAO.getAllAtividades();
        if (atividadeAdapter == null) {
            atividadeAdapter = new AtividadeAdapter(this, listaAtividades);
            listViewAtividades.setAdapter(atividadeAdapter);
        } else {
            atividadeAdapter.notifyDataSetChanged();
        }
    }

    public void abrirSobre(View view){
        SobreActivity.sobre(this);
    }

    public void adicionarAtividade(View view){
        MainActivity.novaAtividade(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String nome = bundle.getString(MainActivity.NOME);
            String prioridade = bundle.getString(MainActivity.PRIORIDADE);
            String status = bundle.getString(MainActivity.STATUS);
            String complexidade = bundle.getString(MainActivity.COMPLEXIDADE);
            String tipo = bundle.getString(MainActivity.TIPOS);

            if (requestCode == MainActivity.NOVO) {
                Atividade novaAtividade = new Atividade(nome, prioridade, status, complexidade, tipo);
                atividadeDAO.insert(novaAtividade);
                listaAtividades.add(novaAtividade);
            } else if (requestCode == MainActivity.ALTERAR) {
                Atividade atividadeAtualizada = listaAtividades.get(posicaoSelecionada);
                atividadeAtualizada.setNome(nome);
                atividadeAtualizada.setPrioridade(prioridade);
                atividadeAtualizada.setStatus(status);
                atividadeAtualizada.setComplexidade(complexidade);
                atividadeAtualizada.setTipo(tipo);
                atividadeDAO.update(atividadeAtualizada);
            }
            atividadeAdapter.notifyDataSetChanged();
        }
    }
}