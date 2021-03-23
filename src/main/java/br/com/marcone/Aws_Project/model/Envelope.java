package br.com.marcone.Aws_Project.model;


import br.com.marcone.Aws_Project.enums.EventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Envelope {

    private EventType event ;
    private String data;
}
