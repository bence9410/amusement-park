package hu.beni.amusementpark.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;

@Data
@Entity
public class Photo {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Lob
	private String photo;

	public Photo(String photo) {
		this.photo = photo;
	}

	protected Photo() {
		super();
	}

}
