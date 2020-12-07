package com.example.apptatuador.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.apptatuador.R;
import com.example.apptatuador.fragment.AgendaFragment;
import com.example.apptatuador.fragment.PostagemFragment;
import com.example.apptatuador.fragment.FeedFragment;
import com.example.apptatuador.fragment.PerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //criando toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Configura bottom navigation view
        configuraBottomNavigation();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //Seta como tela inicial o 1° fragment
        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_pesquisar:
                startActivity(new Intent(this, PesquisarActivity.class));
                break;
            case R.id.menu_mensagens:
                startActivity(new Intent(this, MensagensActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void configuraBottomNavigation(){
        BottomNavigationViewEx bottomNavigationView = findViewById(R.id.include4);

        //config iniciais
        bottomNavigationView.enableAnimation(true);
        bottomNavigationView.enableItemShiftingMode(true);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.setTextVisibility(true);
        bottomNavigationView.setTextSize(11);

        habilitarNavegacao(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);//seleciona o frame inicial
        menuItem.setChecked(true);
    }

    //metodo para tratar evento de click no BottomNavigation
    private void habilitarNavegacao(BottomNavigationViewEx bottomView){
        bottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()){
                    case R.id.ic_feed:
                        fragmentTransaction.add(R.id.viewPager, new FeedFragment()).commit();
                        return true;
                    case R.id.ic_perfil:
                        fragmentTransaction.add(R.id.viewPager, new PerfilFragment()).commit();
                        return true;
                    case R.id.ic_camera:
                            fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                        return true;
                    case R.id.ic_agenda:
                        fragmentTransaction.add(R.id.viewPager, new AgendaFragment()).commit();
                        return true;
                }
                return false;
            }
        });
    }
}