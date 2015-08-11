package bib.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class LogFile {

    private File file;
    private FileOutputStream fos;

    public FileOutputStream getFos() {
        return fos;
    }

    public void setFos(FileOutputStream fos) {
        this.fos = fos;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public LogFile(String destination, String nameFile) {
        try {
            File folder = new File(destination);
            folder.mkdirs();
            this.setFile(new File(destination + nameFile));
            this.setFos(new FileOutputStream(this.file));
        } catch (FileNotFoundException e) {
            System.err.println("Erro encontrado: " + e.getMessage());
        }
    }

    public void CloseReport() {
        try {
            this.fos.close();
        } catch (IOException e) {
            System.err.println("Erro encontrado: " + e.getMessage());
        }
    }

    public void add(String text) {
        try {
            text += ";";
            this.fos.write(text.getBytes());
        } catch (IOException e) {
            System.err.println("Erro encontrado: " + e.getMessage());
        }
    }

    public void ln() {
        try {
            String text = "\n";
            this.fos.write(text.getBytes());
        } catch (IOException e) {
            System.err.println("Erro encontrado: " + e.getMessage());
        }
    }

}
