commonOravg=c(0, 0, 8, 9, 22, 1, 1, 11, 0, 1, 5, 0)
indices = c(1, 4, 7, 11, 13, 15, 16, 17, 23, 27, 18, 19)

ttt <- read.table("features1", header=FALSE, sep = ',')
ttt = as.matrix(ttt)
ncolumn = ncol(ttt)
nrows = nrow(ttt)
res = matrix(, nrow = nrows, ncol=ncolumn)
j=1
for (i in c(1:ncolumn)) {
  tmp = ttt[,i]
  if (i %in% indices) {
    tmp[tmp=="\tnull" | tmp=="null"] = commonOravg[j]
    j = j+1
  }  
  res[,i] = sapply(tmp, as.numeric)
}

features = res[,c(2:ncol(res))]
features = scale(features, center=TRUE, scale=TRUE)
data = data.frame(res[,1], features)
write.table(data, file="scale_features1", sep=", ", row.names=FALSE, col.names=FALSE)
