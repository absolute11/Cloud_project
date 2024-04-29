package ru.cloudproject.cloud.cloudtest.dto;

public class FileNameAndSizeDTO {
    private String name;
    private int size;
    public FileNameAndSizeDTO(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
