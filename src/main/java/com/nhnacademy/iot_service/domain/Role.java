package com.nhnacademy.iot_service.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "roles")
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_no", nullable = false)
    @Comment("권한번호")
    private Long roleNo;

    @EqualsAndHashCode.Include
    @Column(name = "role_name", nullable = false, length = 50, unique = true)
    @Comment("권한명")
    private String roleName;

    @Column(name = "role_description", nullable = false, length = 200, unique = true)
    @Comment("권한설명")
    private String roleDescription;

    private Role(String roleName, String roleDescription) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

    public static Role ofNewRole(String roleName, String roleDescription){
        return new Role(roleName, roleDescription);
    }

    /**
     * 권한 정보를 업데이트하는 메서드입니다.
     * 이 메서드는 역할 이름과 역할 설명을 수정할 때 사용됩니다.
     *
     * @param roleName 수정할 권한 이름
     * @param roleDescription 수정할 권한 설명
     */
    public void update(String roleName, String roleDescription) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

}