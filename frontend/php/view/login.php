<?php
require_once(dirname(__FILE__)."/../conf/properties.php");
?>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>DNSE3</title>

        <!-- Bootstrap -->
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/lib/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet">

        <!-- Custom styles -->
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/css/common.css" rel="stylesheet">
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/css/login.css" rel="stylesheet">

        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
          <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->

        <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <!-- Include all compiled plugins (below), or include individual files as needed -->
        <script src="<?php echo $CONF_PROP["dnse3_root"]?>/lib/bootstrap-3.3.7/js/bootstrap.min.js"></script>   

    </head>
    <body>
        <div id="sticky-wrapper">
        <div class="container login-container">
            <header class="row header">
                <div id="div-login-logo" class="col-sm-8 col-sm-offset-2 col-md-6 col-md-offset-3 col-lg-4 col-lg-offset-4">
                    <!--<h1 id="logo">DNSE3</h1>-->
                    <img id="login-logo" alt="DNSE3" src="<?php echo $CONF_PROP["dnse3_root"];?>/images/dnse3.svg">
                </div>
            </header>
            <div class="row">
                <div class="col-sm-8 col-sm-offset-2 col-md-6 col-md-offset-3 col-lg-4 col-lg-offset-4">
                    <?php
                    $panel_type = 'panel-default';
                    if (isset($unable_to_connect_remote_server) && $unable_to_connect_remote_server == true) {
                        $panel_type = ' panel-danger" ';
                    }
                    ?>
                    <div class="login-panel panel <?php echo $panel_type; ?>">
                        <div class="panel-heading">
                            <h3 class="panel-title">Inicia sesión</h3>
                        </div>
                        <div class="panel-body">
                            <?php
                            if (isset($error_credentials) && $error_credentials==true){
                                echo '<div class="alert alert-danger" role="alert">Los credenciales son incorrectos</div>';
                            }
                            if (isset($unable_to_connect) && $unable_to_connect== true){
                                echo '<div class="alert alert-danger" role="alert">No se puede conectar con el servidor LDAP</div>';
                            }
                            if (isset($unable_to_connect_remote_server) && $unable_to_connect_remote_server == true){
                                echo '<div class="alert alert-danger" role="alert">No se puede conectar con el servidor remoto. Podría estar fuera de servicio de forma temporal.</div>';
                            }
                            ?>
                            <form class="form-signin" method="post" action="<?php echo $CONF_PROP["dnse3_root"]?>/php/controller/login.php">
                                <fieldset>
                                    <div class="form-group">
                                        <label for="loginUsername" class="sr-only">Usuario</label>
                                        <input type="text" id="loginUsername" name="loginUsername" class="form-control" placeholder="Usuario" required autofocus>
                                    </div>
                                    <div class="form-group">
                                        <label for="loginPassword" class="sr-only">Contraseña</label>
                                        <input type="password" id="loginPassword" name="loginPassword" class="form-control" placeholder="Contraseña" required>
                                    </div>
                                    <?php
                                    $status = '';
                                    if (isset($unable_to_connect_remote_server) && $unable_to_connect_remote_server == true){
                                        $status = ' disabled="disabled" ';
                                    }
                                    ?>
                                    <button class="btn btn-lg btn-primary btn-block" type="submit" <?php echo $status;?> >Entrar</button>
                                </fieldset>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>
            <?php
                require_once(dirname(__FILE__) . "/footer.php");
            ?>
    </body>
</html>
