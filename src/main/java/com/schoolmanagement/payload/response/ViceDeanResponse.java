package com.schoolmanagement.payload.response;

import com.schoolmanagement.payload.response.abstracts.BaseUserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter // @Data da yazilabilir
@Setter
@NoArgsConstructor // SuperBuilder oldugu icin AllArgsConstructor gerek yok
@SuperBuilder
public class ViceDeanResponse extends BaseUserResponse {
}
