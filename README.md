# PayGo Java
---
Exemplo de integração em Java com a biblioteca PGWebLib da plataforma de transações com cartão da PayGo Web.

## Ambiente e configuração da aplicação

Para o funcionamento da aplicação e correto reconhecimento da biblioteca de integração disponibilizada pela Pay&Go, algumas detalhes devem ser observados:
* JVM **32 bits**;
* Deve ser utilizado **Java 8**;
* O arquivo da *PGWebLib.dll* (incluso no repositório) deve ser incluído nua pasta mapeada no path do sistema. Preferencialmente na pasta SysWOW64 (*C:\Windows\SysWOW64*);
* A pasta do projeto deve possuir permissão de leitura e escrita;
* É necessário possuir um cadastro no ambiente de testes/sandbox da Pay&Go;
* PIN-pad com ponto de captura válido.

---

**Obs:** O projeto deve ser importado como *Maven Project* dentro da sua IDE de preferência.

----

Ao executar a aplicação, será utilizado o mesmo diretório do projeto para salvar os arquivos relativos à comunição da Automação com a infraestrutura da Pay&Go. Neste diretório serão criadas duas pastas:
* __Data:__ pasta utilizada pela lib para registrar arquivos internos.
* __Log:__ pasta onde ficam os arquivos de log da execução da aplicação e da comunicação com o PIN-pad.

## Execução

Para executar a aplicação é necessário, primeiramente, chamar o método **init()** da biblioteca e, em seguida, realizar a instalação do PIN-pad.
Neste exemplo foram implementadas duas formas de interação com a biblioteca PGWebLib: via **linha de comando** e via **interface gráfica**.

### Linha de comando
Crie uma instância da classe CMDInterface e realize a chamada aos métodos disponiblizados (listagem pode ser consultada na interface *UserInterface*).

### Interface gráfica
Na interface gráfica é possível selecionar a operação que se deseja realizar através do seletor na parte superior a esquerda.
Podem ser adicionados parâmetros a serem enviados na operação selecionado. Só é possível adicionar métodos que constem na documentação fornecida pela Pay&Go.
Além disso, a aplicação permite simular a solicitação de informações específicas do cliente através da opção **Capturar dados usando PIN-pad**.
![Interface gráfica](ui.png)

---

## Mais informações

- [Site Pay&Go](https://www.paygo.com.br)
- [dev@paygo.com.br](dev@paygo.com.br)
- 0800 737 2255 - opção 1