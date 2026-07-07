package com.homeservices.service;

import com.homeservices.domain.Categoria;
import com.homeservices.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarActivas() {
        return categoriaRepository.findByActivoTrueOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria obtener(Long id) {
        return categoriaRepository.findById(id).orElse(new Categoria());
    }

    @Transactional
    public void guardar(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    @Transactional
    public void desactivar(Long id) {
        categoriaRepository.findById(id).ifPresent(categoria -> {
            categoria.setActivo(false);
            categoriaRepository.save(categoria);
        });
    }
}
