package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@ToString(of = "name")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class Team {

    public Team(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    List<Member> members = new ArrayList<>();
}
