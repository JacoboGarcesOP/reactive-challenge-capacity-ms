package co.com.bancolombia.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "capacity", schema = "capacity_schema")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CapacityEntity {
  @Id
  @Column("capacity_id")
  private Long id;

  @Column("name")
  private String name;

  @Column("description")
  private String description;
}
