package inss.gca.mogi.mogi.util;

import inss.gca.mogi.mogi.model.Servidor;

public class Sessao {
    private static Servidor servidorLogado;

    public static void setServidor(Servidor servidor) {
        servidorLogado = servidor;
    }

    public static Servidor getServidor() {
        return servidorLogado;
    }

    public static int getMatricula() {
        if (servidorLogado != null) {
            return servidorLogado.getMatricula();
        }
        return 0;
    }
}
