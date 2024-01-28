exports.handler = async (event) => {
    if (event.Records) {
        event.Records.forEach(record => {
            console.log('Mensagem Recebida:', record.body);
        });
    } else {
        console.log('Nenhum registro para processar');
    }
    return { event: event };
};
