package org.tecno.gestor.biblioteca.model;

import java.util.List;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

@Entity
@Table(name = "Categoria")
public class Categoria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_categoria;

    @Column(unique = true)
    private String categoria;

    @OneToMany(mappedBy = "categoria")
    @Column(nullable = true)
    private List<Libro> libro = new ArrayList<>();

    public Categoria() {
    }

    public Categoria(String categoria) {
        this.categoria = categoria;
    }

    public long getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(long id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<Libro> getLibro() {
        return libro;
    }

    public void setLibro(List<Libro> libro) {
        this.libro = libro;
    }
}
