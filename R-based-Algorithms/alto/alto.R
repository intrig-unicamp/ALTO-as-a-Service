if(!require(data.table)){
        install.packages("data.table")
        library(data.table)
}

bgp.rib.m.anas.filter <- function(input.data, output.path){
        
        if (file.exists(input.data)){
                if (file.info(input.data)$isdir){     
                        files <- list.files(input.data, full.names = TRUE)
                } else {
                        files <- input.data
                }
        } else {
                stop(paste(input.data, " not found.", sep=""))
        }
        
        i <- 0 
        for(f in files){
                
                file.name <- gsub(".*/","", sub("\\.[[:alnum:]]+$", "", f))
                
                file.name <- sub(".csv", "", file.name)
        
                cat("Processing ", f)
                
                file.output <- paste(output.path, "/", file.name, 
                                         sep = "")
                
                rib <- data.table(read.csv(f))
                
                tryCatch({
                        rib.alto.wau <- rib[,.(network, netmask, path)]
                        rib.alto.wau$path <- 
                                sapply(rib.alto.wau$path, 
                                       function(x) substring(x, regexpr("[0-9]*$", x)[1]))
                        rib.alto.wau <- unique(rib.alto.wau)
                        rib.alto.wau <- rib.alto.wau[!rib.alto.wau$path == ""]
                        
                        ## Unique and without aggregation
                        tryCatch({
                                dup <-  duplicated(rib.alto.wau, 
                                                   by=c("network","netmask")) | 
                                        duplicated(rib.alto.wau, 
                                                   fromLast = TRUE, 
                                                   by=c("network","netmask"))
                                
                                if (! file.exists(output.path)){
                                        dir.create(output.path, recursive = TRUE)
                                }
                                
                                gzfile <- gzfile(paste(file.output,".wau.csv.gz",sep=""), "w")
                                write.csv(rib.alto.wau[!dup,], 
                                          gzfile, 
                                            row.names = FALSE, quote=FALSE, col.names = FALSE,
                                            sep=',')
                                close(gzfile)
                        }, error =  function(e) {
                                cat("\n")
                                cat("ERROR wau: ",file.output,".wau.csv")
                        })
                        
                        ## Repeted network and mask
                        tryCatch({
                                rib.alto.rep <- rib.alto.wau[dup,]
                                gzfile <- gzfile(paste(file.output,".rep.csv.gz",sep=""), "w")
                                write.csv(rib.alto.rep, 
                                            gzfile, 
                                    row.names = FALSE, quote=FALSE, col.names = FALSE, 
                                    sep=',')
                                close(gzfile)
                        }, error =  function(e) {
                                cat("\n")
                                cat("ERROR rep: ",file.output,".rep.csv")
                        })
                        
                        ## Aggregated Path
                        tryCatch({
                                rib.alto.agg <- 
                                        rib[,.(network, netmask, 
                                               path)][rib$path %like% "\\{|\\}|\\(|\\)"]
                                
                                #######################################################
                                ##change column type
                                rib.alto.agg$network = as.factor(rib.alto.agg$network)
                                rib.alto.agg$netmask = as.integer(rib.alto.agg$netmask)
                                rib.alto.agg$path = as.character (rib.alto.agg$path)
                                #######################################################
                                
                                rib.alto.agg$path <- 
                                        sapply(rib.alto.agg$path, 
                                               function(x) substring(x, 
                                                      regexpr("([0-9]* \\{.*\\}$)|([0-9]* \\(.*\\)$)", 
                                                                                x)[1]))
                                
                                rib.alto.agg <- unique(rib.alto.agg)
                                gzfile <- gzfile(paste(file.output,".agg.csv.gz",sep=""), "w")
                                write.csv(rib.alto.agg, 
                                          gzfile, 
                                            row.names = FALSE, quote=FALSE, 
                                            col.names = FALSE, sep=',')
                                close(gzfile)
                        }, error =  function(e) {
                                cat("\n")
                                cat("ERROR agg: ",file.output,".agg.csv")
                        })
                }, error =  function(e) {
                        cat("\n")
                        cat("ERROR FILE")
                })
                cat("\n\n")
        }
}



