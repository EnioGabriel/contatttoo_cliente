package com.example.apptatuador.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.apptatuador.R;
import com.example.apptatuador.fragment.ConsultaAbertaFragment;
import com.example.apptatuador.fragment.ConsultaPrivadaFragment;
import com.example.apptatuador.fragment.PropostasAceitasFragment;
import com.example.apptatuador.fragment.TatuagemFinalizadaFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MensagensActivity extends AppCompatActivity {

    private SmartTabLayout smartTabLayoutConsulta;
    private ViewPager viewPagerConsulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagens);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        //btnVoltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        smartTabLayoutConsulta = findViewById(R.id.smartTabConsulta);
        viewPagerConsulta = findViewById(R.id.viewPagerConsulta);

        //COnfigurando smartTab
        getSupportActionBar().setElevation(0);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Consultas Abertas", ConsultaAbertaFragment.class)
                .add("Consultas privadas", ConsultaPrivadaFragment.class)
                .add("Propostas Aceitas", PropostasAceitasFragment.class)
                .add("Finalizados", TatuagemFinalizadaFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerConsulta);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.smartTabConsulta);
        viewPagerTab.setViewPager(viewPager);

    }
    //corrigindo btnVoltar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}