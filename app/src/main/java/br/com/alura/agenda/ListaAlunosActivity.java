package br.com.alura.agenda;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
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

        carregarLista();


        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);
                //Toast.makeText(ListaAlunosActivity.this, "Aluno " + aluno.getNome() + " clicado", Toast.LENGTH_SHORT);
                Intent intentVaiParaFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
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
        ArrayAdapter<Aluno> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alunos);
        listaAlunos.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

        carregarLista();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {

        final MenuItem ligar = menu.add("Ligar");
        final MenuItem enviarSms = menu.add("Enviar SMS");
        final MenuItem enviarEmail = menu.add("Enviar e-mail");
        MenuItem navegar = menu.add("Ir para site");
        final MenuItem localizar = menu.add("Localizar no mapa");
        MenuItem compartilhar = menu.add("Compartilhar");
        MenuItem excluir = menu.add("Excluir");


        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);


        navegar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent irParaNavegador = new Intent(Intent.ACTION_VIEW);
                Uri site = Uri.parse("http://" + aluno.getSite());
                irParaNavegador.setData(site);
                startActivity(irParaNavegador);

                return false;
            }
        });


        compartilhar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "assunto do que será compartilhado");
                intent.putExtra(Intent.EXTRA_TEXT, "texto do que será compartilhado");
                startActivity(Intent.createChooser(intent, "Escolha como compartilhar"));

                return false;
            }
        });

        localizar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                Intent intentMapa = new Intent(Intent.ACTION_VIEW);
                intentMapa.setData(Uri.parse("geo:0,0?z=14&q=" + aluno.getEndereco()));
                localizar.setIntent(intentMapa);

                return false;
            }
        });


        ligar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent irParaTelaDiscagem = new Intent(Intent.ACTION_DIAL);
                Uri telefone = Uri.parse("tel:" + aluno.getTelefone());
                irParaTelaDiscagem.setData(telefone);
                if (ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //startActivity(irParaTelaDiscagem);
                    ligar.setIntent(irParaTelaDiscagem);
                }

                return false;
            }
        });


        enviarEmail.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intentEmail = new Intent(Intent.ACTION_SEND);
                intentEmail.setType("message/rfc822");
                intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"danielsbezerra@gmail.com"});
                intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Elogios do curso de android");
                intentEmail.putExtra(Intent.EXTRA_TEXT, "Este curso é ótimo!!!");
                enviarEmail.setIntent(intentEmail);
                return false;
            }
        });

        enviarSms.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent irParaTelaSms = new Intent(Intent.ACTION_VIEW);
                Uri telefone = Uri.parse("sms:" + aluno.getTelefone());
                irParaTelaSms.setData(telefone);
                if (ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    enviarSms.setIntent(irParaTelaSms);
                }

                /*
                //Envia SMS direto da aplicação
                SmsManager smsManager = SmsManager.getDefault();
                PendingIntent sentIntent = PendingIntent.getActivity(ListaAlunosActivity.this, 0, null, 0);

                if (PhoneNumberUtils.isWellFormedSmsAddress(aluno.getTelefone())) {
                    smsManager.sendTextMessage(aluno.getTelefone(), null, "Sua nota é " + aluno.getNota(), sentIntent, null);
                    Toast.makeText(ListaAlunosActivity.this,"SMS enviado com sucesso!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ListaAlunosActivity.this, "Falha no envio do SMS.", Toast.LENGTH_LONG).show();
                }
*/
                return false;
            }
        });

        excluir.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
