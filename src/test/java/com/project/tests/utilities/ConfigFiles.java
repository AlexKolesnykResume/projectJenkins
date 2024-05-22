package com.project.tests.utilities;

import java.io.File;
import java.util.Properties;

public class ConfigFiles {
    String configFilesPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
            + "resources" + File.separator +"ApplicationFiles" + File.separator + "ConfigFiles" + File.separator;
    File configPropertiesFile = new File(configFilesPath + "Config.properties");
    File dbConfigFile = new File(configFilesPath + "DBConfig.properties");
    FileUtil fileUtil = new FileUtil();

    private Properties configFileProperties(File file){
        return fileUtil.returnProperties(file);
    }

    public Properties getDBConfigFileProperties(){
        return configFileProperties(configPropertiesFile);
    }

    public Properties readConfigFile(String fileName){
        File configFile = new File(configFilesPath + fileName + ".properties");
        return configFileProperties(configFile);
    }

    /**  //PropertiesConfiguration-->huge class that is a jar file

    public void storeConfigFile(String fileName, String key, String value){
        String filePath = configFilepath + fileName + ".properties"; //properties

        PropertiesConfiguration config = null;
        try{
            fu.createpropertyFile(filePath);
        }catch (IOException e){
            e.printStackTrace();
        }
        try{
            config = new PropertiesConfiguration(filePath);
            config.setProperty(key, value);
            config.save;
        }catch (ConfigurationException e){
            System.out.println("Exception in file store");
        }
    }
     */
}
