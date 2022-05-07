package grooteogi.mapper;

public interface BasicMapper<D, E> {
  D toDto(E entity);

  E toEntity(D dto);

}
