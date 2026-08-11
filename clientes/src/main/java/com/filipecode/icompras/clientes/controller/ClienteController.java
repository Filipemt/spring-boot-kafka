package com.filipecode.icompras.clientes.controller;

import com.filipecode.icompras.clientes.controller.dto.ClientePatchRequest;
import com.filipecode.icompras.clientes.controller.dto.ClienteRequest;
import com.filipecode.icompras.clientes.controller.dto.ClienteResponse;
import com.filipecode.icompras.clientes.controller.dto.FiltroCliente;
import com.filipecode.icompras.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> salvar(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.salvar(request));
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String email,
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(clienteService.listar(new FiltroCliente(nome, cpf, email), pageable));
    }

    @GetMapping("{codigo}")
    public ResponseEntity<ClienteResponse> obterDados(@PathVariable("codigo") Long codigo) {
        return ResponseEntity.ok(clienteService.obterPorCodigo(codigo));
    }

    @PutMapping("{codigo}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable("codigo") Long codigo, @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.atualizar(codigo, request));
    }

    @PatchMapping("{codigo}")
    public ResponseEntity<ClienteResponse> atualizarParcial(@PathVariable("codigo") Long codigo, @Valid @RequestBody ClientePatchRequest request) {
        return ResponseEntity.ok(clienteService.atualizarParcial(codigo, request));
    }

    @DeleteMapping("{codigo}")
    public ResponseEntity<Void> deletar(@PathVariable("codigo") Long codigo) {
        clienteService.deletar(codigo);
        return ResponseEntity.noContent().build();
    }
}
