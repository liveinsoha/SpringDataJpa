package study.datajpa.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@Getter
@Setter
@ToString
public class MemberDto {

    private Long id;
    private String name;
    private int age;
    private String teamName;

    public MemberDto(Long id, String name, String teamName) {
        this(id, name, 0, teamName);
    }

    public MemberDto(Long id, String name, int age, String teamName) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.teamName = teamName;
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.age = member.getAge();
        setTeamName(member.getTeam());
    }

    private void setTeamName(Team team) {
        if (team != null) {
            this.teamName = team.getName();
            return;
        }
        this.teamName = null;
    }


}
