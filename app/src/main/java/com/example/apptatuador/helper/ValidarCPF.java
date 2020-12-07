package com.example.apptatuador.helper;

public class ValidarCPF {

    public static boolean isCPF(String CPF) {

        int digito10 = 0, digito11 = 0;
        int cpfArray[] = new int[11];

        //checando se o tamanho é 11
        if (CPF.length() != 11) {
            return false;
        }

        // Eliminando falsos positivos
        if (CPF.equals("00000000000") /*|| CPF.equals("11111111111")*/ || CPF.equals("22222222222") ||
                CPF.equals("33333333333") || CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") || CPF.equals("88888888888") ||
                CPF.equals("99999999999")) {
            return (false);
        }

        //Preenchendo array e convertendo String to Int
        for (int i = 0; i < 11; i++)
            cpfArray[i] = Integer.parseInt(CPF.substring(i, i + 1));

        //Pegando a logica dos ultimos 9 digitos e atribuindo na variavel
        for (int i = 0; i < 9; i++)
            digito10 += cpfArray[i] * (i + 1);

        //Armazenando o valor convertido
        cpfArray[9] = digito10 = digito10 % 11;

        //Pegando a logica dos ultimos 10 digitos e atribuindo na variavel
        for (int i = 0; i < 10; i++)
            digito11 += cpfArray[i] * i;

        //Armazenando o valor convertido
        cpfArray[10] = digito11 = digito11 % 11;

        if (digito10 > 9)
            cpfArray[9] = 0;

        if (digito11 > 9)
            cpfArray[10] = 0;

        if (Integer.parseInt(CPF.substring(9, 10)) != cpfArray[9] || Integer.parseInt(CPF.substring(10, 11)) != cpfArray[10])
            return false;
        else
            return true;
    }
}