# TP2 - Refatoração

BuildPipeline Refactoring Kata in Java
======================================

O código original se trata de um analisador de estados de testes e deploy de um suposto projeto instanciado ao passar pelo método principal 'run' da classe Pipeline.
- Inicialmente temos algumas interfaces e contratos que formam o esqueleto do processo, no caso as interfaces Config, Emailer e Logger.
- Temos também a classe Project, que é basicamente a classe do objeto a ser processado, o domínio.
- E enums para facilitar a baixa acoplação e padronização de algumas condições do domínio. Sendo eles DeploymentEnvironment e TestStatus.
- Na parte de Testes temos a implementação da inteface Logger na classe CapturingLogger. Dando uma idéia de possibilidade de como realizar os testes, mas sem nenhum teste implementado na classe PipelineTestes.

Antes de começar a refatoração do código, foram implementados testes para validar a estrutura inicial do código. 
Estes testes cobrem o método 'run' da classe Pipeline no qual ocorre o processo principal da aplicação.

### Refatoração 1

A primeira refatoração foi feita na classe principal Pipeline, mais especificamente no método 'run'. E foi realizada por causa da dificuldade de apreender o sentido da cadeia de condicionais formada pelos if elses contidos na função.

### Refatoração 2

A segunda refatoração se deu na classe ‘Project’ onde foi separada em arquivos diferentes o builder e o domínio, além de ter sido feitas algumas simplificações como a classe projeto ter os atributos que originalmente ficavam contidos no builder, que causava uma certa confusão entre o domínio e seu builder, e a utilização de um switch expressivo, além de acrescentar a palavra chave final nos atributos já que não há alteração de estado após o builder.  

### Refatoração 3

A terceira refatoração foi apenas para estabelecer as strings “success” e “failure” como enums, evitando um problema de referênciamento no código, além de explicitar do que se trata o resultado.