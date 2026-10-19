package com.ansub.tvhome;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// The home screen's own labels, in the TV's language. The English text is the key, so a label with
// no translation stays in English. To add a language, add a table below and register it in LANGS.
final class Strings {
    private static final Map<String, String[]> LANGS = new HashMap<>();
    static {
        LANGS.put("pt", new String[] { // Brazilian Portuguese
            "Settings", "Configurações",
            "Add app", "Adicionar app",
            "Network", "Rede",
            "Offline", "Sem conexão",
            "Sound", "Som",
            "Picture", "Imagem",
            "Move", "Mover",
            "Replace", "Substituir",
            "Remove from home", "Remover do início",
            "Uninstall", "Desinstalar",
            "App info", "Informações do app",
            "Use the arrows to move it, then press OK", "Use as setas para mover e depois aperte OK",
            "Every app is already here", "Todos os apps já estão aqui",
            "Add an app", "Adicionar um app",
            "Replace %s", "Substituir %s",
            "EEEE, d MMMM", "EEEE, d 'de' MMMM", // the date above the clock
        });
    }

    private Strings() {}

    static String lang() { return Locale.getDefault().getLanguage(); }

    // The label in the TV's language, else the English one. Read on every call, so it follows the TV.
    static String t(String en) {
        String[] table = LANGS.get(lang());
        if (table != null)
            for (int i = 0; i < table.length; i += 2) if (table[i].equals(en)) return table[i + 1];
        return en;
    }
}
