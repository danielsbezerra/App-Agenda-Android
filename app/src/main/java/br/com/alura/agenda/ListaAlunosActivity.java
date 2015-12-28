package br.com.alura.agenda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.alura.agenda.DAO.AlunoDAO;
import br.com.alura.agenda.model.Aluno;

public class ListaAlunosActivity extends AppCompatActivity {

    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);
                //Toast.makeText(ListaAlunosActivity.this, "Aluno " + aluno.getNome() + " clicado", Toast.LENGTH_SHORT);
                Intent intentVaiParaFormulario  = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                intentVaiParaFormulario.putExtra("aluno", aluno);
                startActivity(intentVaiParaFormulario);

            }
        });


        Button novoAluno = (Button) findViewById(R.id.novo_aluno);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intent);
            }
        });

        registerForContextMenu(listaAlunos);

    }

    private void carregarLista() {
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.get();
        dao.close();
        listaAlunos = (ListView) findViewById(R.id.lista_alunos);
        ArrayAdapter<Aluno> adapter = new ArrayAdapter<Aluno>(this, android.R.layout.simple_list_item_1, alunos);
        listaAlunos.setAdapter(adapter);


//        String[] alunos = {"Daniel","Ricardo","Zé", "Carlos"};
//        ListView listaAlunos = (ListView) findViewById(R.id.lista_alunos);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alunos);
//        listaAlunos.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        carregarLista();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem excluir = menu.add("Excluir");
        //MenuItem editar = menu.add("Editar");

        excluir.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.delete(aluno);
                dao.close();

                Toast.makeText(ListaAlunosActivity.this, "Aluno " + aluno.getNome() + " excluído", Toast.LENGTH_SHORT).show();

                carregarLista();

                return false;
            }
        });

    }
}
