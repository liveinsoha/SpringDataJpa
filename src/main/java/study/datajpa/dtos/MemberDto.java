package study.datajpa.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDto {

    private Long id;
    private String name;
    private int age;
    private String teamName;

    public MemberDto(Long id, String name, String teamName) {
       this(id,name,0,teamName);
    }

    public MemberDto(Long id, String name, int age, String teamName) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.teamName = teamName;
    }
}
