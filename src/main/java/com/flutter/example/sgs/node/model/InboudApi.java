package com.flutter.example.sgs.node.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Setter
@Getter
public class InboudApi implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
}
