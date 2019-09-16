# PayGo Java
---
Exemplo de integração em Java com a biblioteca PGWebLib da plataforma de transações com cartão da PayGo Web.

## Ambiente e configuração da aplicação

Para o funcionamento da aplicação e correto reconhecimento da biblioteca de integração disponibilizada pela Pay&Go, algumas detalhes devem ser observados:
* JVM deve ser **32 bits**
* Deve ser utilizado **Java 8**
* O arquivo da PGWebLib.dll (incluso no repositório) deve ser incluído numa pasta mapeada no path do sistema. Preferencialmente na pasta SysWOW64 (*C:\Windows\SysWOW64*)
* A pasta do projeto deve possuir permissão de leitura e escrita

O projeto deve ser importado como *Maven Project* dentro da sua IDE de preferência.

Ao executar a aplicação, será utilizado o mesmo diretório do projeto para salvar os arquivos relativos à comunição da Automação com a infraestrutura da Pay&Go. Neste diretório serão criadas duas pastas:
* __Data:__ pasta utilizada pela lib para registrar arquivos internos.
* __Log:__ pasta onde ficam os arquivos de log da execução da aplicação e da comunicação com o PIN-pad.