package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString(of = {"id", "age", "name"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@NamedQuery(name = "Member.findByName",
        query = "select m from Member m where m.name = :name")
public class Member extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    private int age;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Embedded
    private Address address;

    public Member(String name) {
        this(name, 0);
    }

    public Member(String name, int age) {
        this(name, age, null);
    }

    public Member(String name, int age, Team team) {
        this(name, age, team, null);
    }

    public Member(String name, int age, Team team, Address address) {
        this.name = name;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
        this.address = address;
    }

    private void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
