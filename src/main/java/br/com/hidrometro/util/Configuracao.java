package br.com.hidrometro.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuracao {
    private final Properties prop = new Properties();

    public Configuracao(String caminhoArquivo) {
        try (InputStream input = Configuracao.class.getClassLoader().getResourceAsStream(caminhoArquivo)) {
            prop.load(input);
        } catch (IOException ex) {
            System.err.println("Erro: Não foi possível carregar o arquivo de configuração.");
            ex.printStackTrace();
        }
    }

    public double getDouble(String chave) {
        return Double.parseDouble(prop.getProperty(chave));
    }

    public int getInt(String chave) {
        return Integer.parseInt(prop.getProperty(chave));
    }

    public String getString(String chave) {
        return prop.getProperty(chave);
    }
}