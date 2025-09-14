package br.com.hidrometro.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuracao {
    private final Properties prop = new Properties();

    public Configuracao(String caminhoArquivo) {
        try (InputStream input = Configuracao.class.getClassLoader().getResourceAsStream(caminhoArquivo)) {
            if (input == null) {
                throw new IOException("Arquivo de configuração não encontrado no classpath: " + caminhoArquivo);
            }
            prop.load(input);
        } catch (IOException ex) {
            System.err.println("Erro fatal: Não foi possível carregar o arquivo de configuração. " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Falha ao inicializar: arquivo de configuração ausente ou ilegível.");
        }
    }

    public double getDouble(String chave) {
        String valor = prop.getProperty(chave);
        if (valor == null) {
            throw new IllegalArgumentException("Chave '" + chave + "' não encontrada no arquivo de configuração.");
        }
        //.trim(),  remove espacos antes de converter
        return Double.parseDouble(valor.trim());
    }

    public int getInt(String chave) {
        String valor = prop.getProperty(chave);
        if (valor == null) {
            throw new IllegalArgumentException("Chave '" + chave + "' não encontrada no arquivo de configuração.");
        }
        //.trim(), remove espacos antes de converter
        return Integer.parseInt(valor.trim());
    }

    public String getString(String chave) {
        String valor = prop.getProperty(chave);
        if (valor == null) {
            throw new IllegalArgumentException("Chave '" + chave + "' não encontrada no arquivo de configuração.");
        }
        //.trim(),  remove espacos
        return valor.trim();
    }
}