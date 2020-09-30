package com.example.producehelper.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StationSelected implements Serializable
{
    private static final long serialVersionUID = 3152709557451878827L;

    private String selected;

    private List<String> stations;
}
