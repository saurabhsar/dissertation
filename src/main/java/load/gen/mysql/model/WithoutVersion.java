package load.gen.mysql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "without_version")
public class WithoutVersion {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "content")
    private String content;
}
