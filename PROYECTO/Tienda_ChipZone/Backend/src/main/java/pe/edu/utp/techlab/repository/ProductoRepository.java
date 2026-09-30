package pe.edu.utp.techlab.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.techlab.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("""
            select p from Producto p
            where (:q = '' or lower(p.nombre) like concat('%', :q, '%')
                           or lower(p.marca) like concat('%', :q, '%'))
              and (:categoria = '' or lower(p.categoria) = :categoria)
            order by p.id
            """)
    List<Producto> buscar(@Param("q") String q, @Param("categoria") String categoria);

    @Query("select distinct p.categoria from Producto p order by p.categoria")
    List<String> categorias();
    boolean existsByNombre(String nombre);
}