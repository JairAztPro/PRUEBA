package pe.edu.utp.techlab.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.techlab.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @EntityGraph(attributePaths = "categoria")
    @Query("""
            select p from Producto p join p.categoria c
            where (:q = '' or lower(p.nombre) like concat('%', :q, '%')
                           or lower(p.marca) like concat('%', :q, '%'))
              and (:categoria = '' or lower(c.nombre) = :categoria)
            order by p.id
            """)
    List<Producto> buscar(@Param("q") String q, @Param("categoria") String categoria);

    @EntityGraph(attributePaths = "categoria")
    List<Producto> findByCategoria_NombreIgnoreCaseOrderByIdAsc(String nombre);

    @Query("select p from Producto p join fetch p.categoria where p.id = :id")
    Optional<Producto> buscarConCategoria(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}