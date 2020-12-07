package com.example.apptatuador.helper;

import android.graphics.Typeface;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import org.threeten.bp.LocalDate;

public class MarcadorDiaAtual implements DayViewDecorator {

    private CalendarDay dia;

    public MarcadorDiaAtual() {
        dia = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dia != null && day.equals(dia);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //Adicionando um estilo negrito para marcar o dia atual
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.4f));
    }

    public void setDia(LocalDate dia) {
        this.dia = CalendarDay.from(dia);
    }
}