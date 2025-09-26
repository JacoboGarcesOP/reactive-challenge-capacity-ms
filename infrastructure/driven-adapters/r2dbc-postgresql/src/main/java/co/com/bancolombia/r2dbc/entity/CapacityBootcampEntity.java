package co.com.bancolombia.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "capacity_bootcamp", schema = "capacity_schema")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CapacityBootcampEntity {
  @Id
  private Long id;
  
  @Column("bootcamp_id")
  private Long bootcampId;
  
  @Column("capacity_id")
  private Long capacityId;
}
