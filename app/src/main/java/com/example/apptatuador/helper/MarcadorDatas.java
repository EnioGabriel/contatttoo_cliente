package com.example.apptatuador.helper;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class MarcadorDatas implements DayViewDecorator {

    private int cor;
    private HashSet<CalendarDay> datas;

    public MarcadorDatas(int cor, Collection<CalendarDay> datas) {
        this.cor = cor;
        this.datas = new HashSet<>(datas);
    }

    @Override
    public boolean shouldDecorate(CalendarDay dia) {
        return datas.contains(dia);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //Adicionando um ponto no dia selecionado
        view.addSpan(new DotSpan(5, cor));
    }
}