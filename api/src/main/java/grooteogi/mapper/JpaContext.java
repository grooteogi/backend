package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.Schedule;
import javax.persistence.EntityManager;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;

public class JpaContext {
  private final EntityManager em;

  private Post parentEntity;

  public JpaContext(EntityManager em) {
    this.em = em;
  }

  @BeforeMapping
  public void setEntity(@MappingTarget Post parentEntity) {
    this.parentEntity = parentEntity;
    // you could do stuff with the EntityManager here
  }

  @AfterMapping
  public void establishRelation(@MappingTarget Schedule childEntity) {
    childEntity.setPost( parentEntity );
    // you could do stuff with the EntityManager here
  }
}
