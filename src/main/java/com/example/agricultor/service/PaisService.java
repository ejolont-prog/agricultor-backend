package com.example.agricultor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.agricultor.model.Pais;
import com.example.agricultor.repository.PaisRepository;

import java.util.List;

@Service
public class PaisService {


    @Autowired
    private PaisRepository paisRepository;


    public List<Pais> findAll() {
        return paisRepository.findAll();
    }


}

