package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Address;
import study.datajpa.entity.Member;
import study.datajpa.dtos.MemberDto;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByNameContainingAndAgeGreaterThan(String name, int age);

    /**
     * 스프링 데이터 JPA는 선언한 "도메인 클래스 + .(점) + 메서드 이름"으로 Named 쿼리를 찾아서 실행
     * 만약 실행할 Named 쿼리가 없으면 메서드 이름으로 쿼리 생성 전략을 사용한다.
     * `@Query` 를 생략하고 메서드 이름만으로 Named 쿼리를 호출할 수 있다.
     */
    @Query(name = "Member.findByName")
    List<Member> findByName(@Param("name") String name);

    /**
     * `@org.springframework.data.jpa.repository.Query` 어노테이션을 사용
     * 실행할 메서드에 정적 쿼리를 직접 작성하므로 이름 없는 Named 쿼리라 할 수 있음
     * JPA Named 쿼리처럼 애플리케이션 실행 시점에 문법 오류를 발견할 수 있음(매우 큰 장점!)
     * 참고: 실무에서는 메소드 이름으로 쿼리 생성 기능은 파라미터가 증가하면 메서드 이름이 매우 지저분해진다. 따
     * 라서 `@Query` 기능을 자주 사용하게 된다.
     */

    @Query("select m from Member m where m.name = :name and m.age = :age")
    List<Member> findByNameAndAge(@Param("name") String name, @Param("age") int age);

    @Query("select m.name from Member m")
    List<String> findUserNameList();

    @Query("select m.address from Member m")
    List<Address> findAddressList();

    @Query("select new study.datajpa.dtos.MemberDto(m.id, m.name, t.name)" +
            " from Member m" +
            " join m.team t")
    List<MemberDto> findMemberDtoList();

    @Query
    Page<Member> findByAge(int age, Pageable pageable);

}
