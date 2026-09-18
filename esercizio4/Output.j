.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 ldc 2
 ldc 3
 ldc 4
 imul 
 iadd 
 ldc 8
 ldc 2
 idiv 
 isub 
 invokestatic Output/print(I)V
 goto L1
L1:
 ldc 10
 istore 0
 goto L2
L2:
 iload 0
 ldc 15
 iadd 
 istore 1
 goto L3
L3:
 iload 1
 invokestatic Output/print(I)V
 goto L4
L4:
 ldc 50
 istore 2
 goto L5
L5:
 iload 2
 ldc 20
 if_icmplt L9
 goto L8
L9:
 ldc 0
 invokestatic Output/print(I)V
 goto L6
L8:
 iload 2
 ldc 50
 if_icmpeq L11
 goto L10
L11:
 ldc 1
 invokestatic Output/print(I)V
 goto L6
L10:
 iload 2
 ldc 100
 if_icmpgt L13
 goto L12
L13:
 ldc 2
 invokestatic Output/print(I)V
 goto L6
L12:
L7:
 ldc 999
 invokestatic Output/print(I)V
 goto L6
L6:
 iload 2
 ldc 0
 if_icmpeq L17
 goto L16
L17:
 ldc 100
 invokestatic Output/print(I)V
 goto L14
L16:
L15:
 ldc 999
 invokestatic Output/print(I)V
 goto L14
L14:
 ldc 0
 istore 3
 goto L18
L18:
L20:
 iload 3
 ldc 3
 if_icmplt L21
 goto L19
L21:
 iload 3
 invokestatic Output/print(I)V
 goto L22
L22:
 iload 3
 ldc 1
 iadd 
 istore 3
 goto L23
L23:
 goto L20
 goto L20
L19:
 ldc 50
 istore 4
 goto L25
L25:
 iload 4
 ldc 2
 imul 
 istore 5
 goto L27
L27:
 iload 5
 invokestatic Output/print(I)V
 goto L28
L28:
 goto L26
L26:
 goto L24
L24:
 goto L0
L0:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

