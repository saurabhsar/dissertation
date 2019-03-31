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
@Table(name = "with_version")
public class WithVersion {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Version
    @Column(name = "content")
    private String content;

    @Version
    @Column(name = "version")
    private int version;
}
