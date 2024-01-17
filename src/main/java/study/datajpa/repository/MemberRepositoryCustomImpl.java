package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import study.datajpa.entity.Member;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    /**
     *사용자 정의 리포지토리 구현체이다
     * 자바에서 제공하는 기능은 아니고 SpringDataJpa 인터페이스가 MemberRepositoryCustom을 상속받으면
     * MemberRepositoryCustomImpl 구현체에서 구현한 메소드를 가져다가 쓴다.
     *
     * 스프링 데이터 2.x 부터는 사용자 정의 구현 클래스에 리포지토리 인터페이스 이름 + `Impl` 을 적용하는 대신에
     * 사용자 정의 인터페이스 명 + `Impl` 방식도 지원한다.
     * 예를 들어서 위 예제의 `MemberRepositoryImpl` 대신에 `MemberRepositoryCustomImpl` 같이 구현해도 된
     * 다.
     *
     * 기존 방식보다 이 방식이 사용자 정의 인터페이스 이름과 구현 클래스 이름이 비슷하므로 더 직관적이다. 추가로 여러
     * 인터페이스를 분리해서 구현하는 것도 가능하기 때문에 새롭게 변경된 이 방식을 사용하는 것을 더 권장한다.
     */
    @Autowired
    private final EntityManager em;

    @Override
    public List<Member> findByCity(String city) {
        return em.createQuery("select m from Member m where m.address.city = :city", Member.class)
                .setParameter("city", city)
                .getResultList();
    }
}
