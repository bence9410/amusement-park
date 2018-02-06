package hu.beni.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveVisitorDTO implements Serializable{

	private static final long serialVersionUID = 7802032004601520550L;

	private Long identifier;

	private String name;

	private String username;

	private LocalDate dateOfBirth;

	private LocalDateTime dateOfSignUp;

	private String state;

}
