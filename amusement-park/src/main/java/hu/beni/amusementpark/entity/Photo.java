package hu.beni.amusementpark.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(of = "id")
public class Photo {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String photo;

	public Photo(String photo) {
		this.photo = photo;
	}

	protected Photo() {
		super();
	}

}
