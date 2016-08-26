/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.libraries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Esta classe é responsável pela busca de um arquivo de propriedades armazenado
 * em disco.
 *
 * <p>
 * O arquivo de propriedades contém os seguintes parâmetros de configuração da
 * aplicação:
 * <ul>
 * <li>versao: Versão da aplicação</li>
 * <li>sdbg: Nome do SGBD utilizado</li>
 * <li>driver: Classe do Driver do SGBD</li>
 * <li>url: Url de conexão com o SGBD</li>
 * <li>databaseName: Nome do banco de dados</li>
 * <li>user: Nome de usuário do SGBD</li>
 * <li>pwd: Senha do usuário no SGBD</li>
 * <li>geoPortalKey: Valor da chave de acesso da API GeoPortal</li>
 * <li>delayAPIThread: Tempo em segundos para um ciclo da Thread de leitura de
 * mensagens</li>
 * <li>delayRuleThread: Tempo em segundos para um ciclo da Thread de leitura das
 * regras</li>
 * <li>limitQuerySize: Quantidade máxima de registros na consulta da tabela de
 * mensagens</li>
 * <li>refreshEntradaTableInterval: Tempo em milissegundos para a atualização da
 * tabela de mensagens de entrada</li>
 * <li>refreshSaidaTableInterval: Tempo em milissegundos para a atualização da
 * tabela de mensagens filtradas</li>
 * </ul>
 *
 * @author Rafael
 *
 */
public class Configuration {

    private static final Properties PARAMETERS = new Properties();

    /**
     * Este método retorna uma instância de {@link Properties} contendo as
     * propriedades de configuração da aplicação.
     *
     * @return Propriedades de configuração da aplicação.
     */
    public static Properties getProperties() {
        Properties prop;
        if (PARAMETERS.isEmpty()) {
            try {
                prop = readPropertyFile("/config/parameters.properties");
                if (prop != null) {
                    PARAMETERS.putAll(prop);
                }
                prop = readPropertyFile("/config/sql.properties");
                if (prop != null) {
                    PARAMETERS.putAll(prop);
                }
            } catch (IOException e) {
                System.err.print(e);
            }
        }
        return PARAMETERS;
    }

    /**
     *
     * Este método busca um arquivo de propriedades em disco, utilizando o
     * caminho fornecido.
     *
     * @param filename Caminho absoluto em disco.
     * @return Uma instância de {@link Properties} lidas do arquivo
     * @throws IOException Indicando erro de leitura.
     */
    private static Properties readPropertyFile(String filename) throws IOException {
        System.out.println("Reading Property File from: " + filename);
        Properties properties = new Properties();
        File propertyFile = new File(filename);
        if (!propertyFile.exists()) {
            System.err.println("File " + filename + " not found in the current directory");
            System.err.println("Trying to load " + filename + " using the classpath");

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            if (is != null) {
                properties.load(is);
            } else {
                System.err.println("File " + filename + " does not exist");
                throw new FileNotFoundException();
            }
        } else {
            FileInputStream fi = new FileInputStream(propertyFile);
            System.out.println("Loaded parameters from file: " + propertyFile.getName());
            properties.load(fi);
            fi.close();
        }

        return properties;
    }

    /**
     * Este método retorna o local do arquivo de configuração.
     *
     * @return String contendo o caminho em disco do arquivo de configuração.
     */
    public static String getPathProperties() {
        Properties prop = Configuration.getProperties();
        if (prop != null) {
            return prop.getProperty("location");
        } else {
            return null;
        }
    }
}
